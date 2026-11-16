import com.zhuofan.core.datasource.JdbcDao;
import com.zhuofan.core.common.CommonFunc;
import com.zhuofan.core.common.CommonObject;
import java.util.HashMap;
import java.util.LinkedHashMap;
import com.zhuofan.core.eval.Eval;
import com.zhuofan.core.orm.model.*;
import com.zhuofan.core.orm.dao.*;
public class C_9a9843fdbb5ca263b0fdb1f72a0c8f17
{
    public Object getEvalResult(HashMap mapRow) throws Exception
    {
        String sResult=mapRow.get("CGMSGINFO").toString();
sResult=String.format("<span onclick=\"layer.alert('"+sResult+"')\">查看</span>",sResult);
return sResult;
    }
}
