import com.zhuofan.core.datasource.JdbcDao;
import com.zhuofan.core.common.CommonFunc;
import com.zhuofan.core.common.CommonObject;
import java.util.HashMap;
import com.zhuofan.core.eval.Eval;
import com.zhuofan.core.orm.model.*;
import com.zhuofan.core.orm.dao.*;
public class C_a217d8c3174ca1f76ef967c3d9f758d5
{
    public Object getEvalResult(HashMap mapRow) throws Exception
    {
        String url="javascript:jsCommon.do_userroleset('"+mapRow.get("USERID")+"');";
return url;

    }
}
