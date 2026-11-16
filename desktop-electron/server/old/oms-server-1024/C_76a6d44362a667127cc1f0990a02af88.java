import com.zhuofan.core.datasource.JdbcDao;
import com.zhuofan.core.common.CommonFunc;
import com.zhuofan.core.common.CommonObject;
import java.util.HashMap;
import java.util.LinkedHashMap;
import com.zhuofan.core.eval.Eval;
import com.zhuofan.core.orm.model.*;
import com.zhuofan.core.orm.dao.*;
public class C_76a6d44362a667127cc1f0990a02af88
{
    public Object getEvalResult(HashMap mapRow) throws Exception
    {
        String url="javascript:pwCallOnce('" + mapRow.get("ID")+ "')";
return url;

    }
}
