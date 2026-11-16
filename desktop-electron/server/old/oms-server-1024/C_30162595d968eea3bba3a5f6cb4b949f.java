import com.zhuofan.core.datasource.JdbcDao;
import com.zhuofan.core.common.CommonFunc;
import com.zhuofan.core.common.CommonObject;
import java.util.HashMap;
import java.util.LinkedHashMap;
import com.zhuofan.core.eval.Eval;
import com.zhuofan.core.orm.model.*;
import com.zhuofan.core.orm.dao.*;
public class C_30162595d968eea3bba3a5f6cb4b949f
{
    public Object getEvalResult(HashMap mapRow) throws Exception
    {
        String sResult=mapRow.get("MSGINFO_1_2").toString();
sResult=String.format("<span onclick=\"layer.alert('"+sResult+"')\">查看</span>",sResult);
return sResult;
    }
}
